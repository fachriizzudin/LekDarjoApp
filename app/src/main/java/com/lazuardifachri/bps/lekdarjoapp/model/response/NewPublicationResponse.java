package com.lazuardifachri.bps.lekdarjoapp.model.response;

import com.google.gson.annotations.SerializedName;
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;

import java.util.List;

public class NewPublicationResponse {
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_items")
    private int totalItems;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("publications")
    private List<Publication> publications;

    public NewPublicationResponse(int totalPages, int totalItems, int currentPage, List<Publication> publications) {
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.publications = publications;
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

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
}

