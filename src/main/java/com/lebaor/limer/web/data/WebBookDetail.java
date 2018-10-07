package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.limer.data.Book;

public class WebBookDetail {
	long bookId;
	String isbn13;
	String isbn10;
	String title;
	String subTitle;
	String author;
	String publisher;
	String publishDate;
	String coverUrl;
	int price;//价格，分
	int pageNum;//页数
	
	String summary;
	String authorIntro;
	String catalog;
	
	public WebBook toWebBook() {
		WebBook wb = new WebBook();
		wb.setAuthor(author);
		wb.setBookId(bookId);
		wb.setCoverUrl(coverUrl);
		wb.setIsbn(isbn13);
		wb.setSubTitle(subTitle);
		wb.setTitle(title);
		return wb;
	}
	
	public void setBookInfo(Book b) {
		this.bookId = b.getId();
		this.isbn10 = b.getIsbn10();
		this.isbn13 = b.getIsbn13();
		this.title = b.getTitle();
		this.subTitle = b.getSubTitle();
		
		if (b.getAuthors() != null) {
			String s = "";
			for (int i = 0; i < b.getAuthors().length(); i++) {
				try {
					String bs = b.getAuthors().getString(i);
					if (s.length() > 0) s += ", ";
					s += bs;
				} catch (Exception e) {
					
				}
			}
			this.author = s;
		}
		
		this.publisher = b.getPublisher();
		this.publishDate = b.getPublishDate();
		this.coverUrl = b.getCoverUrl();
		this.price = b.getPrice();
		this.pageNum = b.getPageNum();
		
		this.summary = b.getSummary();
		this.authorIntro = b.getAuthorIntro();
		this.catalog = b.getCatalog();
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("bookId", bookId);
			o.put("isbn13", isbn13);
			o.put("isbn10", isbn10);
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("author", author);
			o.put("publisher", publisher);
			o.put("publishDate", publishDate);
			o.put("coverUrl", coverUrl);
			o.put("price", price);
			o.put("pageNum", pageNum);
			o.put("summary", summary);
			o.put("authorIntro", authorIntro);
			o.put("catalog", catalog);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBookDetail parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}

	public static WebBookDetail parseJSON(JSONObject o) {
		try {
			WebBookDetail n = new WebBookDetail();
			n.bookId = o.getLong("bookId");
			n.isbn13 = o.getString("isbn13");
			n.isbn10 = o.getString("isbn10");
			n.title = o.getString("title");
			n.subTitle = o.getString("subTitle");
			n.author = o.getString("author");
			n.publisher = o.getString("publisher");
			n.publishDate = o.getString("publishDate");
			n.coverUrl = o.getString("coverUrl");
			n.price = o.getInt("price");
			n.pageNum = o.getInt("pageNum");
			n.summary = o.getString("summary");
			n.authorIntro = o.getString("authorIntro");
			n.catalog = o.getString("catalog");
			return n;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String toString() {
		return toJSON();
	}
	
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public String getIsbn13() {
		return isbn13;
	}
	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}
	public String getIsbn10() {
		return isbn10;
	}
	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAuthorIntro() {
		return authorIntro;
	}
	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	
}
