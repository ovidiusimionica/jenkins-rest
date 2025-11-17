/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.jenkins.rest.parsers;


import static java.nio.charset.StandardCharsets.UTF_8;

import com.cdancy.jenkins.rest.domain.job.ProgressiveText;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Optional;

public class ResponseResult<T>
{
    private final int status;
    private final T entity;
    private final String entityRaw;

    private ResponseResult(int status, T entity, String error)
    {
        this.status = status;
        this.entity = entity;
        this.entityRaw = error;
    }

    public static <T> ResponseResult<T> of(Response response, T value, String error)
    {
        return new ResponseResult<>(response.getStatus(), value, error);
    }

    public static <T> ResponseResult<T> of(Response response, Class<T> type)
    {
        T entity = null;
        String error = null;
        if (isSuccess(response.getStatus()))
        {
            entity = response.readEntity(type);

        } else
        {
            error = response.readEntity(String.class);
        }
        return new ResponseResult<>(response.getStatus(), entity, error);
    }

    public static ResponseResult<Void> ofVoid(Response response)
    {
        String error = null;
        if (!isSuccess(response.getStatus()))
        {
            error = response.readEntity(String.class);
        }
        return new ResponseResult<>(response.getStatus(), null, error);
    }

    public static ResponseResult<Integer> ofInt(Response response)
    {
        Integer entity = null;
        String error = null;
        if (isSuccess(response.getStatus()))
        {
            entity = Integer.parseInt(response.readEntity(String.class));
        } else
        {
            error = response.readEntity(String.class);
        }
        return new ResponseResult<>(response.getStatus(), entity, error);
    }


    public static ResponseResult<ProgressiveText> ofProgressiveText(Response response)
    {
        ProgressiveText entity = null;
        String error = null;
        if (isSuccess(response.getStatus()))
        {
            try (InputStream entityStream = (InputStream) response.getEntity())
            {
                String text = new String(entityStream.readAllBytes(), UTF_8);

                int size = Optional.ofNullable(response.getHeaderString("X-Text-Size"))
                    .map(Integer::parseInt)
                    .orElse(-1);

                boolean moreData = Optional.ofNullable(response.getHeaderString("X-More-Data"))
                    .map(Boolean::parseBoolean)
                    .orElse(false);

                entity = new ProgressiveText(text, size, moreData);
            } catch (Exception e)
            {
                error = "Failed to parse ProgressiveText: " + e.getMessage();
            } finally
            {
                response.close();
            }
        } else
        {
            error = response.readEntity(String.class);
        }

        return new ResponseResult<>(response.getStatus(), entity, error);
    }


    public static ResponseResult<SystemInfo> ofSystemInfo(Response response)
    {
        if (response == null)
        {
            throw new IllegalArgumentException("Response cannot be null");
        }

        int status = response.getStatus();
        if (isSuccess(status))
        {
            SystemInfo info = new SystemInfo(
                response.getHeaderString("X-Hudson"),
                response.getHeaderString("X-Jenkins"),
                response.getHeaderString("X-Jenkins-Session"),
                response.getHeaderString("X-Instance-Identity"),
                response.getHeaderString("X-SSH-Endpoint"),
                response.getHeaderString("Server"),
                null
            );
            return new ResponseResult<>(status, info, null);
        } else
        {
            String error = response.readEntity(String.class);
            return new ResponseResult<>(status, null, error);
        }
    }

    private static boolean isSuccess(int status)
    {
        return status >= 200 && status < 400;
    }

    public boolean isSuccess()
    {
        return isSuccess(status);
    }

    public int getStatus()
    {
        return status;
    }

    public T getEntity()
    {
        return entity;
    }

    public String getError()
    {
        return entityRaw;
    }
}
