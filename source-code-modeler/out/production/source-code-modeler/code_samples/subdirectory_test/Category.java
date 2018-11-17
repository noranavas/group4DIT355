package org.recipelibrary.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id // Makes every other non-transient field marked as a column. I.e. no need to mark the rest of the fields with @Column.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 3, max = 25, message = "Ergh ima da evil witch, no name such, must be 3-25 long!")
    private String name;

    @NotNull
    @Pattern(regexp = "#[0-9a-fA-F]{6}", message = "Kha! Witch no like no color, select 1!")
    private String colorCode;

    @OneToMany(mappedBy = "category") // One category object can be associated with many recipe objects.
    private List<Recipe> recipes = new ArrayList<>();

    //===== Constructor(s) =====//
    // Default constructor needed for h2 database to work.
    public Category() {

    }

    // Only used by CategoryRepository(?).
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //===== Getters & Setters =====//
    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

}