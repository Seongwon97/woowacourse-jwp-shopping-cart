package woowacourse.shoppingcart.exception;

public class InvalidProductException extends RuntimeException {

    public InvalidProductException() {
        super("올바르지 않은 사용자 이름이거나 상품 아이디 입니다.");
    }
}
