package shared.messages;

public record FileTransferOffer(String sender, String filename, long size,String sessionId) {
}
