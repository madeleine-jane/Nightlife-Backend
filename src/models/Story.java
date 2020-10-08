package models;

import java.util.ArrayList;

public class Story implements StatusCollection {
    public Story(ArrayList<Status> list) {
        for (int i = 0; i < list.size(); ++i) {
            statusList.add(list.get(i));
        }
    }
    public Story() {

    }
}
