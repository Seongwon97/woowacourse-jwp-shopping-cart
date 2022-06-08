package woowacourse.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.auth.application.AuthService;
import woowacourse.auth.dto.LoginRequest;
import woowacourse.member.dto.FindMemberInfoResponse;
import woowacourse.member.dto.SignUpRequest;
import woowacourse.member.dto.UpdateNameRequest;
import woowacourse.member.dto.UpdatePasswordRequest;
import woowacourse.member.exception.InvalidMemberEmailException;
import woowacourse.member.exception.InvalidMemberNameException;
import woowacourse.member.exception.InvalidPasswordException;
import woowacourse.member.exception.MemberNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class MemberServiceTest {

    private final MemberService memberService;
    private final AuthService authService;

    public MemberServiceTest(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @DisplayName("올바른 데이터로 회원가입에 성공한다.")
    @Test
    void signUp() {
        assertDoesNotThrow(
                () -> memberService.signUp(new SignUpRequest("pobi@wooteco.com", "포비", "Javajigi!"))
        );
    }

    @DisplayName("중복된 이메일이 존재하는 경우 예외가 발생한다.")
    @Test
    void signUpWithDuplicateEmail() {
        assertThatThrownBy(
                () -> memberService.signUp(new SignUpRequest("ari@wooteco.com", "가짜아리", "Wooteco!"))
        ).isInstanceOf(InvalidMemberEmailException.class)
                .hasMessageContaining("중복되는 이메일이 존재합니다.");
    }

    @DisplayName("올바른 id로 회원정보를 조회한다.")
    @Test
    void findMemberInfo() {
        FindMemberInfoResponse response = memberService.findMemberInfo(1L);

        assertAll(
                () -> assertThat(response.getEmail()).isEqualTo("ari@wooteco.com"),
                () -> assertThat(response.getName()).isEqualTo("아리")
        );
    }

    @DisplayName("존재하지 않는 id로 회원을 찾는 경우 예외가 발생한다.")
    @Test
    void findMemberInfoWithNotExistId() {
        assertThatThrownBy(
                () -> memberService.findMemberInfo(100L)
        ).isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @DisplayName("올바른 id로 회원 이름을 변경한다.")
    @Test
    void updateName() {
        memberService.updateName(1L, new UpdateNameRequest("메아리"));
        assertThat(memberService.findMemberInfo(1L).getName()).isEqualTo("메아리");
    }

    @DisplayName("현재이름과 같은 이름으로 변경시 예외가 발생한다.")
    @Test
    void updateNameWithSameName() {
        assertThatThrownBy(
                () -> memberService.updateName(1L, new UpdateNameRequest("아리"))
        ).isInstanceOf(InvalidMemberNameException.class)
                .hasMessageContaining("현재 이름과 같은 이름으로 변경할 수 없습니다.");
    }

    @DisplayName("올바른 id로 회원 비밀번호를 변경한다.")
    @Test
    void updatePassword() {
        memberService.updatePassword(1L, new UpdatePasswordRequest("Wooteco1!", "NewPassword1!"));
        assertThat(authService.authenticate(new LoginRequest("ari@wooteco.com", "NewPassword1!"))).isEqualTo(1L);
    }

    @DisplayName("현재 비밀번호와 일치하지 않을시 예외가 발생한다.")
    @Test
    void updatePasswordWithIncorrectPassword() {
        assertThatThrownBy(
                () -> memberService.updatePassword(1L, new UpdatePasswordRequest("wrongPassword!", "NewPassword1"))
        ).isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("현재 비밀번호와 일치하지 않습니다.");
    }

    @DisplayName("현재 비밀번호와 같은 비밀번호로 변경시 예외가 발생한다.")
    @Test
    void updatePasswordWithSamePassword() {
        assertThatThrownBy(
                () -> memberService.updatePassword(1L, new UpdatePasswordRequest("Wooteco1!", "Wooteco1!"))
        ).isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("현재 비밀번호와 같은 비밀번호로 변경할 수 없습니다.");
    }

    @DisplayName("올바른 id로 회원 정보를 삭제한다.")
    @Test
    void withdraw() {
        memberService.withdraw(1L);

        assertThatThrownBy(
                () -> memberService.findMemberInfo(100L)
        ).isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @DisplayName("존재하지 않는 id로 삭제하려는 경우 예외가 발생한다.")
    @Test
    void withdrawWithNotExistId() {
        assertThatThrownBy(
                () -> memberService.withdraw(100L)
        ).isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }
}
