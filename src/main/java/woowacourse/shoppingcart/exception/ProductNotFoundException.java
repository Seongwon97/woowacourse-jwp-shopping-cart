package woowacourse.shoppingcart.exception;

public class ProductNotFoundException extends IllegalArgumentException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
