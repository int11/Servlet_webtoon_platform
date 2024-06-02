import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Webtoon {
	private int aid;
	private String title;
	private String thumbnail;
	private List<String> images = new ArrayList<>();
	private String date;
}
