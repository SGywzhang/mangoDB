package massage.cs4224c.document;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Timestamp {

    private long timestamp;

    public Timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("$date")
    public long getTimestamp() {
        return timestamp;
    }
}
