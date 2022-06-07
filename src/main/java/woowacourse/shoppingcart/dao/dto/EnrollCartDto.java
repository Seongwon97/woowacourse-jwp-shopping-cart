package woowacourse.shoppingcart.dao.dto;

public class EnrollCartDto {

    private final Long memberId;
    private final Long productId;
    private final int quantity;

    public EnrollCartDto(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = 1;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}