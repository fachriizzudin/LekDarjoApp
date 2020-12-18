package com.lazuardifachri.bps.lekdarjoapp.util;

import com.lazuardifachri.bps.lekdarjoapp.model.Category;
import com.lazuardifachri.bps.lekdarjoapp.model.District;
import com.lazuardifachri.bps.lekdarjoapp.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class Parameters {

    private static Parameters instance = new Parameters();
    private List<Subject> subjects = new ArrayList<>();
    private List<Category> socialCategories = new ArrayList<>();
    private List<Category> economyCategories = new ArrayList<>();
    private List<Category> agricultureCategories = new ArrayList<>();
    private List<District> districts = new ArrayList<>();;

    private Parameters() {

        Subject kependudukan = new Subject(1, "Sosial dan Kependudukan");
        Subject ekonomi = new Subject(2, "Ekonomi dan Perdagangan");
        Subject pertanian = new Subject(3, "Pertanian dan Pertambangan");

        subjects.add(new Subject(999, "Semua"));
        subjects.add(kependudukan);
        subjects.add(ekonomi);
        subjects.add(pertanian);
        subjects.add(new Subject(4, "Umum"));

        socialCategories.add(new Category(999, "Semua", kependudukan));
        socialCategories.add(new Category(1, "Agama", kependudukan));
        socialCategories.add(new Category(2, "Geografi", kependudukan));
        socialCategories.add(new Category(3, "Iklim", kependudukan));
        socialCategories.add(new Category(4, "Indeks Pembangunan Manusia", kependudukan));
        socialCategories.add(new Category(5, "Kemiskinan", kependudukan));
        socialCategories.add(new Category(6, "Kependudukan", kependudukan));
        socialCategories.add(new Category(7, "Kesehatan", kependudukan));
        socialCategories.add(new Category(8, "Konsumsi dan Pengeluaran", kependudukan));
        socialCategories.add(new Category(9, "Pemerintahan", kependudukan));
        socialCategories.add(new Category(10, "Pendidikan", kependudukan));
        socialCategories.add(new Category(11, "Perumahan", kependudukan));
        socialCategories.add(new Category(12, "Politik dan Keamanan", kependudukan));
        socialCategories.add(new Category(13, "Sosial Budaya", kependudukan));
        socialCategories.add(new Category(14, "Tenaga Kerja", kependudukan));

        economyCategories.add(new Category(999, "Semua", ekonomi));
        economyCategories.add(new Category(15, "Energi", ekonomi));
        economyCategories.add(new Category(16, "Industri", ekonomi));
        economyCategories.add(new Category(17, "Inflasi", ekonomi));
        economyCategories.add(new Category(18, "Keuangan", ekonomi));
        economyCategories.add(new Category(19, "Komunikasi", ekonomi));
        economyCategories.add(new Category(20, "Konstruksi", ekonomi));
        economyCategories.add(new Category(21, "Pariwisata", ekonomi));
        economyCategories.add(new Category(22, "Produk Domestik Regional Bruto", ekonomi));
        economyCategories.add(new Category(23, "Transportasi", ekonomi));

        agricultureCategories.add(new Category(999, "Semua", pertanian));
        agricultureCategories.add(new Category(24, "Hortikultura", pertanian));
        agricultureCategories.add(new Category(25, "Perikanan", pertanian));
        agricultureCategories.add(new Category(26, "Perkebunan", pertanian));
        agricultureCategories.add(new Category(27, "Pertambangan", pertanian));
        agricultureCategories.add(new Category(28, "Peternakan", pertanian));
        agricultureCategories.add(new Category(29, "Tanaman Pangan", pertanian));

        districts.add(new District("0","Semua"));
        districts.add(new District("3515010","Tarik"));
        districts.add(new District("3515020","Prambon"));
        districts.add(new District("3515030","Krembung"));
        districts.add(new District("3515040","Porong"));
        districts.add(new District("3515050"," Jabon"));
        districts.add(new District("3515060","Tanggulangin"));
        districts.add(new District("3515070","Candi"));
        districts.add(new District("3515080","Tulangan"));
        districts.add(new District("3515090","Wonoayu"));
        districts.add(new District("3515100","Sukodono"));
        districts.add(new District("3515110","Sidoarjo"));
        districts.add(new District("3515120","Buduran"));
        districts.add(new District("3515130","Sedati"));
        districts.add(new District("3515140","Waru"));
        districts.add(new District("3515150","Gedangan"));
        districts.add(new District("3515160","Taman"));
        districts.add(new District("3515170","Krian"));
        districts.add(new District("3515180","Balong"));
        districts.add(new District("3515","Umum"));

    }

    public static Parameters getInstance() {
        if (instance == null) {
            instance = new Parameters();
        }
        return instance;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public List<Category> getSocialCategories() {
        return socialCategories;
    }

    public List<Category> getEconomyCategories() {
        return economyCategories;
    }

    public List<Category> getAgricultureCategories() {
        return agricultureCategories;
    }

    public List<District> getDistricts() {
        return districts;
    }
}
