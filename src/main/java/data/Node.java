package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author lvcc
 */
@Data
@AllArgsConstructor
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;

    private int port;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Node otherNode = (Node) obj;
        return StringUtils.isNotBlank(otherNode.getIp()) &&
                otherNode.getIp().equals(ip) &&
                otherNode.getPort() == port;
    }
}
