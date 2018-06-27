package cz.zcu.students.kiwi.popApp.pop3;

public class ResponseParser {
    public Response parse(String message) {
        if(message == null || message.length() == 0) {
            throw new IllegalArgumentException("Message must be a non empty string");
        }

        Response.Status status = Response.Status.ofMessage(message);
        String rest = message.substring(status.protocolRepresentation().length());

        Response response = new Response(status, rest.split("\n"));
        response.setRaw(message);

        return response;
    }
}
