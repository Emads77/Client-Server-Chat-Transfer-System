package shared.messages;

public record FileDownloadReady(int port, String sessionId, String checksum){
}
