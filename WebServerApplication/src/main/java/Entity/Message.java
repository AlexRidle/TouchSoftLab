package Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String from;
    private String to;
    private String content;
    private String timestamp;

    public Message(final String from, final String content, final String timestamp) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) &&
                Objects.equals(to, message.to) &&
                Objects.equals(this.content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, content);
    }
}
