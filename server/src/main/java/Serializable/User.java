package Serializable;

import java.io.Serializable;

public class User implements Serializable {
    public String name;
    public String passwd;
    public int winCnt = 0;
    public int totalCnt = 0;
}
