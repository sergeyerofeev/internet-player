package sample.model;

import sample.model.InternetStream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "MediaList")
public class ListWrapper {
    private List<InternetStream> list;

    @XmlElement(name = "Item")
    public List<InternetStream> getList() {
        return list;
    }

    public void setList(List<InternetStream> list) {
        this.list = list;
    }
}
