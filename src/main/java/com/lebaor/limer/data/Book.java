package com.lebaor.limer.data;

import org.json.JSONArray;
import org.json.JSONObject;


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
	
	String publisher;
	String publishDate;//出版日期
	int price;//分
	String coverUrl;//https://img1.doubanio.com\/view\/subject\/m\/public\/s29518349.jpg
	
	String bookId;//豆瓣的id
	int pageNum;
	String authorIntro;
	String summary;
	String catalog;
	
	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public void setBookFrom(String bookFrom) {
		this.bookFrom = bookFrom;
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

	public void setJson(String json) {
		this.json = json;
		try {
			this.obj = new JSONObject(json);
			this.isbn10 = this.obj.getString("isbn10");
			this.isbn13 = this.obj.getString("isbn13");
			this.title = this.obj.getString("title");
			this.subTitle = this.obj.getString("alt_title");
			this.authors = this.obj.getJSONArray("author");
			this.tags = this.obj.getJSONArray("tags");
			this.publisher = this.obj.getString("publisher");
			this.publishDate = this.obj.getString("pubdate");
			this.bookId = this.obj.getString("id");
			this.authorIntro = this.obj.getString("author_intro");
			this.summary = this.obj.getString("summary");
			this.catalog = this.obj.getString("catalog");
			this.coverUrl = this.obj.getJSONObject("images").getString("large");
			this.pageNum = Integer.parseInt(this.obj.getString("pages"));
			this.price = (int)(Float.parseFloat(this.obj.getString("price"))*100);
		} catch (Exception e) {
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

	public String getBookId() {
		return bookId;
	}

	public String getBookFrom() {
		return bookFrom;
	}

	public int getPageNum() {
		return pageNum;
	}
	
	
}
