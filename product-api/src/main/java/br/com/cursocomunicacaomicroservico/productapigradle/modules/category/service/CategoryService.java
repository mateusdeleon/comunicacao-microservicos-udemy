package br.com.cursocomunicacaomicroservico.productapigradle.modules.category.service;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.ValidationException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto.CategoryRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto.CategoryResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.model.Category;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.repository.CategoryRepository;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;

    public CategoryResponse findByIdCategory(Integer id) {
        return CategoryResponse.of(findById(id));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::of)
                .toList();
    }

    public List<CategoryResponse> findByDescription(String description) {
        if (isEmpty(description)) {
            throw new ValidationException("the category description was not informed.");
        }

        return categoryRepository.findByDescriptionContainingIgnoreCase(description)
                .stream()
                .map(CategoryResponse::of)
                .toList();
    }

    public Category findById(Integer id) {
        validateInformedId(id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given id."));
    }

    public CategoryResponse save(CategoryRequest categoryRequest) {
        validateCategoryDescription(categoryRequest);
        var category = categoryRepository.save(Category.of(categoryRequest));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest categoryRequest, Integer id) {
        validateCategoryDescription(categoryRequest);
        validateInformedId(id);
        var category = Category.of(categoryRequest);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("you cannot delete this category because it's already defined by a product.");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("the category was deleted.");
    }

    private void validateCategoryDescription(CategoryRequest categoryRequest) {
        if (isEmpty(categoryRequest.getDescription())) {
            throw new ValidationException("the category description was not informed.");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("the category id must be informed.");
        }
    }

}
