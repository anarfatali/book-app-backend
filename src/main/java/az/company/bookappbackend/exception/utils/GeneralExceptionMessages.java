package az.company.bookappbackend.exception.utils;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GeneralExceptionMessages {
    AZ(
            "az",
            "Texniki səhv var!\nZəhmət olmasa, biraz sonra yenidən cəhd edin."
    ),
    EN(
            "en",
            "Error!\nPlease, try again later."
    ),
    RU(
            "ru",
            "Ошибка!\nПожалуйста, попробуйте позже."
    );

    private final String langCode;
    private final String message;

    GeneralExceptionMessages(
            String langCode,
            String message
    ) {
        this.langCode = langCode;
        this.message = message;
    }

    public static String getExceptionByCode(String languageCode) {
        return Arrays
                .stream(values())
                .filter(p -> p.langCode.equals(languageCode))
                .findFirst()
                .orElse(AZ)
                .getMessage();
    }
}
