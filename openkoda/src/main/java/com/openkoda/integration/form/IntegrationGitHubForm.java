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

package com.openkoda.integration.form;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.model.dto.IntegrationGitHubDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

public class IntegrationGitHubForm extends AbstractEntityForm<IntegrationGitHubDto, IntegrationModuleOrganizationConfiguration>
        implements LoggingComponentWithRequestId {
    public IntegrationGitHubForm() {
        super(new IntegrationGitHubDto(), null, IntegrationFrontendMappingDefinitions.gitHubConfigurationForm);
    }

    public IntegrationGitHubForm(IntegrationGitHubDto dto, IntegrationModuleOrganizationConfiguration entity) {
        super(dto, entity, IntegrationFrontendMappingDefinitions.gitHubConfigurationForm);
    }

    public IntegrationGitHubForm(FrontendMappingDefinition formDef) {
        super(new IntegrationGitHubDto(), null, formDef);
    }

    public IntegrationGitHubForm(IntegrationGitHubDto dto, IntegrationModuleOrganizationConfiguration entity, FrontendMappingDefinition formDef) {
        super(dto, entity, formDef);
    }

    @Override
    public IntegrationGitHubForm populateFrom(IntegrationModuleOrganizationConfiguration entity) {
        dto.setGitHubRepoName(entity.getGitHubRepoName());
        dto.setGitHubRepoOwner(entity.getGitHubRepoOwner());
        return this;
    }

    @Override
    protected IntegrationModuleOrganizationConfiguration populateTo(IntegrationModuleOrganizationConfiguration entity) {
        entity.setGitHubRepoName(getSafeValue(entity.getGitHubRepoName(), GITHUB_REPO_NAME_));
        entity.setGitHubRepoOwner(getSafeValue(entity.getGitHubRepoOwner(), GITHUB_REPO_OWNER_));
        return entity;
    }

    @Override
    public IntegrationGitHubForm validate(BindingResult br) {
        if (StringUtils.isBlank(dto.getGitHubRepoName())) {
            br.rejectValue("dto.gitHubRepoName", "not.empty");
        }
        if (StringUtils.isBlank(dto.getGitHubRepoOwner())) {
            br.rejectValue("dto.gitHubRepoOwner", "not.empty");
        }
        return this;
    }
}
