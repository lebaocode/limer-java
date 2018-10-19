package com.lebaor.limer.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.LogUtil;


/**
 * 豆瓣等其他地方拿到的书籍信息，不含本产品相关信息
 * @author lixjl
 *
 */
public class Book {
	long id;
	String isbn10;
	String isbn13;
	String json;//json数组
	long createTime;
	String bookFrom = "douban";//谁提供的书籍信息，比如豆瓣
	
	JSONObject obj;
	String title;
	String subTitle;
	JSONArray authors;//json数组 ["[美] 伊恩·古德费洛","[加] 约书亚·本吉奥","[加] 亚伦·库维尔"]
	JSONArray tags;//json数组 [{"count":464,"name":"机器学习","title":"机器学习"},{"count":241,"name":"计算机","title":"计算机"}]
	
	JSONArray translators;
	String publisher;
	String publishDate;//出版日期
	int price;//分
	String coverUrl;//https://img1.doubanio.com\/view\/subject\/m\/public\/s29518349.jpg
	
	String doubanBookId;//豆瓣的id
	int pageNum;
	String authorIntro;
	String summary;
	String catalog;
	
	int raterNum;//评价人数
	String rating;//分数
	
	String seriesId;//所属系列id(doubanid)
	String seriesTitle; //所属系列标题
	
	public String getAuthorAsString() {
		
		if (authors == null) return "";
		
		String s = "";
		try {
			for (int i =0;i < authors.length(); i++) {
				String a = authors.getString(i);
				if (s.length() > 0) s += "，";
				s += a;
			}
			return s;
		} catch (Exception e) {
			return s;
		}
	}
	
	public void setBookFrom(String bookFrom) {
		this.bookFrom = bookFrom;
	}
	
	public String getIsbn() {
		return getIsbn13();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}
	
	public static Book parseFromDoubanJSON(String json) {
		Book b = new Book();
		b.setDoubanJson(json);
		return b;
	}
	
	public String toDoubanJSON() {
		try {
			return this.json;
		} catch(Exception e) {
			return "{}";
		}
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("isbn10", isbn10);
			o.put("isbn13", isbn13);
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("author", this.getAuthorAsString());
			o.put("tags", tags);
			o.put("translators", translators);
			o.put("publisher", publisher);
			o.put("publishDate", publishDate);
			o.put("price", price);
			o.put("coverUrl", coverUrl);
			o.put("doubanBookId", doubanBookId);
			o.put("pageNum", pageNum);
			o.put("authorIntro", authorIntro);
			o.put("summary", summary);
			o.put("catalog", catalog);
			o.put("raterNum", raterNum);
			o.put("rating", rating);
			o.put("seriesId", seriesId);
			o.put("seriesTitle", seriesTitle);
			
			if (this.price < 680) {
				this.price = 680;
			}
			
			int limerFee = LimerConstants.computeLogisticsFee(this.pageNum);
			if (limerFee > this.price * 0.3) {
				limerFee = (int)(this.price * 0.3);
			}
			if (limerFee < this.price * 0.05) {
				limerFee = (int)(this.price * 0.05);
			}
			if (limerFee < 300) limerFee = 300;
			o.put("limerFee", limerFee/100 + ".00");
			o.put("price", String.format("%.2f", (float)price/100));
			return o.toString();
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("Book.toJSON() error for isbn=" + isbn13, e);
			return "{error: 'format error.'}";
		}
		
	}

	public void setDoubanJson(String json) {
		this.json = json;
		try {
			this.obj = new JSONObject(json);
			this.isbn10 = JSONUtil.getString(this.obj, "isbn10");
			this.isbn13 = JSONUtil.getString(this.obj, "isbn13");
			this.title = JSONUtil.getString(this.obj, "title");
			this.subTitle = JSONUtil.getString(this.obj, "alt_title");
			this.authors = JSONUtil.getJSONArray(this.obj, "author");
			this.translators = JSONUtil.getJSONArray(this.obj, "translator");
			this.tags = JSONUtil.getJSONArray(this.obj, "tags");
			this.publisher = JSONUtil.getString(this.obj, "publisher");
			this.publishDate = JSONUtil.getString(this.obj, "pubdate");
			this.doubanBookId = JSONUtil.getString(this.obj, "id");
			this.authorIntro = JSONUtil.getString(this.obj, "author_intro");
			this.summary = JSONUtil.getString(this.obj, "summary");
			this.catalog = JSONUtil.getString(this.obj, "catalog");
			this.coverUrl =  JSONUtil.getString(JSONUtil.getJSONObject(this.obj, "images"), "medium");
			
			String pageStr = JSONUtil.getString(this.obj, "pages");
			if (pageStr.endsWith("页")) pageStr = pageStr.substring(0, pageStr.length() - 1).trim();
			try {
				this.pageNum = Integer.parseInt(pageStr);
			} catch (Exception ex) {
				this.pageNum = 0;
			}
			
			String priceStr = JSONUtil.getString(this.obj, "price").trim();
			if (priceStr.endsWith("元")) priceStr = priceStr.substring(0, priceStr.length() - 1).trim();
			if (priceStr.endsWith("新臺幣")) priceStr = priceStr.substring(0, priceStr.length() - 3).trim();
			if (priceStr.startsWith("CNY")) priceStr = priceStr.substring(4).trim();
			if (priceStr.length() == 0) priceStr = "0";
			try {
				if (priceStr.startsWith("USD")) {
					this.price = (int)(Float.parseFloat(priceStr.substring(4).trim())* 690);
				} else {
					this.price = (int)(Float.parseFloat(priceStr)*100);
				}
			} catch (Exception ex) {
				this.price = 0;
			}
			
			this.raterNum = JSONUtil.getInt(JSONUtil.getJSONObject(this.obj, "rating"), "numRaters");
			this.rating = JSONUtil.getString(JSONUtil.getJSONObject(this.obj, "rating"), "average");
			this.seriesId = JSONUtil.getString(JSONUtil.getJSONObject(this.obj, "series"), "id");
			this.seriesTitle = JSONUtil.getString(JSONUtil.getJSONObject(this.obj, "series"), "title");
			
			this.setCreateTime(System.currentTimeMillis());
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("Book.setDoubanJson() error for isbn=" + json, e);
			this.obj = null;
		}
	}
	
	public String getAuthorIntro() {
		return authorIntro;
	}

	public String getSummary() {
		return summary;
	}

	public String getCatalog() {
		return catalog;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getIsbn10() {
		return isbn10;
	}

	public String getIsbn13() {
		return isbn13;
	}

	public String getTitle() {
		return title;
	}

	public String getSubTitle() {
		return subTitle;
	}


	public JSONArray getAuthors() {
		return authors;
	}

	public JSONArray getTags() {
		return tags;
	}
	
	public String[] getTagsString() {
		if (tags == null) return new String[0];
		try {
			String[] arr = new String[tags.length()];
			for (int i = 0; i < tags.length(); i++) {
				arr[i] = JSONUtil.getString(tags.getJSONObject(i), "title");
			}
			return arr;
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	public String getPublisher() {
		return publisher;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public int getPrice() {
		return price;
	}

	public String getCoverUrl() {
		return coverUrl;
	}
	
	public String getBookFrom() {
		return bookFrom;
	}

	public int getPageNum() {
		return pageNum;
	}

	public int getRaterNum() {
		return raterNum;
	}

	public String getRating() {
		return rating;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public String getSeriesTitle() {
		return seriesTitle;
	}

	public JSONArray getTranslators() {
		return translators;
	}

	public String getDoubanBookId() {
		return doubanBookId;
	}
	
	
}
