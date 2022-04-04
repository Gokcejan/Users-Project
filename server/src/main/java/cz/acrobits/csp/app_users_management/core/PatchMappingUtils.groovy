package cz.acrobits.csp.app_users_management.core

class PatchMappingUtils {

    static void mapField(String dtoFieldName, String entityFieldName, dto, entity) {

        if (dto."${dtoFieldName}") {
            entity."${entityFieldName}" = dto."${dtoFieldName}"
        } else if (dto.isFieldUpdated(dtoFieldName)) {
            entity."${entityFieldName}" = null
        }
    }
}
