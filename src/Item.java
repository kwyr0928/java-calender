public class Item {
	private String year;
	private String month;
	private String day;
	private String context;
	private String start;
	private String finish;
	private String sort;
	private String detail;

	public Item(String year, String month, String day, String context, String start, String finish, String sort, String detail) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.context = context;
		this.start = start;
		this.finish = finish;
		this.sort = sort;
		this.detail = detail;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	public String getDay() {
		return day;
	}

	public String getContext() {
		return context;
	}

	public String getStart() {
		return start;
	}

	public String getFinish() {
		return finish;
	}

	public String getSort() {
		return sort;
	}

	public String getDetail() {
		return detail;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	} 
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	public String toString() {
		if(start == null || finish == null) {
			if(sort == null && detail == null) {
		return context + "  詳細未設定";
			}
			return context + "  分類:" + sort + "  詳細:" + detail;
		}else {
		return "アルバイト " + start + "～" + finish;
		}
	}
}
