package br.com.cursocomunicacaomicroservico.productapigradle.modules.category.controller;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto.CategoryRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto.CategoryResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryRequest);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("{id}")
    public CategoryResponse findById(@PathVariable Integer id) {
        return categoryService.findByIdCategory(id);
    }

    @GetMapping("description/{description}")
    public List<CategoryResponse> findByDescription(@PathVariable String description) {
        return categoryService.findByDescription(description);
    }

    @PutMapping("{id}")
    public CategoryResponse update(@RequestBody CategoryRequest categoryRequest, @PathVariable Integer id) {
        return categoryService.update(categoryRequest, id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }

}
