package shared.messages;

public record FileTransferReq(String receiver, String filename, long size) {
}
