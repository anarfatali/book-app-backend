package az.company.bookappbackend.storage_service;

public class FileUtility {

    public static String determineContentTypeFromFileName(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();

        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseFileName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCaseFileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerCaseFileName.endsWith(".svg")) {
            return "image/svg+xml";
        }

        return "application/octet-stream"; // Default fallback
    }

    public static String getFileExtensionSafe(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        String cleanFileName = fileName.trim();
        int lastDotIndex = cleanFileName.lastIndexOf('.');

        // No dot, or dot is at the beginning/end, or multiple dots at end
        if (lastDotIndex <= 0 || lastDotIndex >= cleanFileName.length() - 1) {
            return "";
        }

        return cleanFileName.substring(lastDotIndex + 1).toLowerCase();
    }

    public static String getFileExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            case "image/jpeg", "image/jpg" -> "jpg";
            default -> "jpg"; // Safe fallback
        };
    }

}
