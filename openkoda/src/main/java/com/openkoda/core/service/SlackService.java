/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.service;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.model.task.HttpRequestTask;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Sends messages to Slack using webHooks. Collaborates with Thymeleaf in message generation(via FrontendResource).
 */
@Service
public class SlackService extends ComponentProvider {

    @Inject
    private TemplateEngine templateEngine;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        debug("[init] Preparing RestTemplate object with headers");
        restTemplate = new RestTemplate();
    }

    public boolean sendToSlackWithCanonical(CanonicalObject object, String templateName, String webHook) {
        debug("[sendToSlackWithCanonical]");
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.canonicalObject, object);
        String message = prepareContent(templateName, model);
        sendMessageToSlack(message, webHook);
        return true;
    }

    public boolean sendMessageToSlack(String message, String webHook) {
        debug("[sendMessageToSlack] Message to {}", webHook);
        String requestJson = String.format("{\"text\":\"%s\"}",
                StringUtils.replace(message, "\"", "\\\""));
        HttpRequestTask httpRequestTask = new HttpRequestTask(webHook, requestJson);
        repositories.unsecure.httpRequest.save(httpRequestTask);
        return true;

    }

    public boolean postMessageToSlackWebhook(String message, String webHook) {
        debug("[postMessageToSlackWebhook]");
        String requestJson = String.format("{\"text\":\"%s\"}",
                StringUtils.replace(message, "\"", "\\\""));
        if(StringUtils.isNotBlank(webHook)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, httpHeaders);
            restTemplate.postForEntity(webHook, httpEntity, String.class);
        }
        return true;
    }

    public String prepareContent(String templateName, Map<String, Object> model) {
        debug("[prepareContent] {}", templateName);
        final Context ctx = new Context(LocaleContextHolder.getLocale());

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(templateName, ctx);
    }
}
