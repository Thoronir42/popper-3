package cz.zcu.students.kiwi.popApp.pop3;

public class Response {

    public enum Status {
        Ok("+OK"), Err("-ERR");

        private String protocolRep;

        Status(String stringRep) {
            this.protocolRep = stringRep;
        }

        public String protocolRepresentation() {
            return this.protocolRep;
        }
    }
}
