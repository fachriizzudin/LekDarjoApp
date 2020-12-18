package com.lazuardifachri.bps.lekdarjoapp.model.response;

import com.google.gson.annotations.SerializedName;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;

import java.util.List;

public class StatisticalNewsResponse {
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_items")
    private int totalItems;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("statistical_news")
    private List<StatisticalNews> statisticalNews;

    public StatisticalNewsResponse(int totalPages, int totalItems, int currentPage, List<StatisticalNews> statisticalNews) {
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.statisticalNews = statisticalNews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<StatisticalNews> getStatisticalNews() {
        return statisticalNews;
    }

    public void setStatisticalNews(List<StatisticalNews> statisticalNews) {
        this.statisticalNews = statisticalNews;
    }
}
