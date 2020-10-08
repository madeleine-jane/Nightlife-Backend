package models;

import java.util.ArrayList;

public class Feed implements StatusCollection {
    public Feed(ArrayList<Status> list) {
        for (int i = 0; i < list.size(); ++i) {
            statusList.add(list.get(i));
        }
    }
    public Feed() {

    }
}
