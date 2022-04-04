package cz.acrobits.csp.app_users_management.provisioning

import cz.acrobits.csp.provisioningsdk.ProvisioningMapper
import org.w3c.dom.Document

import static org.apache.commons.lang3.StringUtils.isBlank

class AppProvisioningMapper extends ProvisioningMapper<AppProvisioningDto> {

    private static final String APP_NAME = "//account/core/title"
    private static final String VISIBLE_URL_SUBDOMAIN = "//conferencing/prefKeys/visibleUrlSubdomain"

    AppProvisioningDto mapFromXml(Document provisioningDocument) {
        AppProvisioningDto dto = new AppProvisioningDto()

        String name = getValueFromXPath(APP_NAME, provisioningDocument)
        dto.name = isBlank(name) ? "App" : name

        String visibleUrlSubdomain = getValueFromXPath(VISIBLE_URL_SUBDOMAIN, provisioningDocument)
        dto.visibleUrlSubdomain = isBlank(visibleUrlSubdomain) ? "example" : visibleUrlSubdomain

        return dto
    }

}
