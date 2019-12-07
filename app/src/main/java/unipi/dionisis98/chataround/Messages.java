package unipi.dionisis98.chataround;

class Messages {
    private String message;
    private String from;

    Messages(String message,String sender){
        this.message = message;
        this.from = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String toString(){
        return this.from +": "+this.message;
    }
}
