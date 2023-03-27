package dev.dividendproject.web;

import dev.dividendproject.service.FinanceService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {
    private final FinanceService financeService;

    // 특정 회사에 해당하는 정보와 배당금을 조회하는 API
    // GET /finance/dividend/{conpanyName}
    // 응답 : {companyName : "좋은 회사", dividend[{date:"2022.3.21 price:"2.00}...]}
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName){
        var result = this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);
    }
}
/*
* ResponseEntity.ok(result)는 HTTP 응답을 나타내는 클래스인
* ResponseEntity의 정적 팩토리 메서드입니다. 이 메서드는
* "200 OK" HTTP 응답 코드를 반환하며, 응답 바디에 result 객체를 담아서 응답합니다.
* 즉, 위 코드는 result 객체를 응답 바디에 담아서 "200 OK" HTTP 응답을 생성하고,
* 이를 나타내는 ResponseEntity 객체를 반환합니다. ResponseEntity
* 객체는 HTTP 응답 코드, 응답 헤더, 응답 바디 등을 포함하는 객체이며,
* 클라이언트는 이를 통해 서버의 응답 결과를 확인할 수 있습니다.
* 또한, ResponseEntity.ok(result)는 ResponseEntity 클래스의
* 다른 정적 팩토리 메서드들과 함께 HTTP 응답 코드, 응답 헤더, 응답 바디 등을
* 자유롭게 설정할 수 있어, 보다 유연한 HTTP 응답을 생성할 수 있습니다.
*/


/*GET과 POST는 HTTP 프로토콜에서 사용되는 두 가지 주요 요청 방법입니다.
https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.11

[GET] GET은 주로 정보를 요청하기 위해 사용됩니다.
 - URL을 통해 데이터를 전달합니다.
 - 데이터는 URL의 쿼리 문자열(query string)에 포함되어 전달됩니다.
 - URL의 길이에 제한이 있으므로, 대부분의 브라우저에서는 약 2000자까지만 전송 가능합니다.
 - GET 요청은 캐싱될 수 있습니다.
 - GET 요청은 브라우저에서 쉽게 캐시할 수 있으므로, 뒤로가기 버튼 등의 브라우저의 기능을 이용할 수 있습니다.

[POST] POST는 주로 서버에 데이터를 제출하기 위해 사용됩니다.
 - HTTP 본문(body)을 통해 데이터를 전달합니다.
 - POST 요청은 URL의 길이에 제한이 없습니다.
 - POST 요청은 캐싱되지 않습니다.
 - POST 요청은 데이터를 서버에 전달할 때 보안성이 더 높습니다.
 - POST 요청은 브라우저에서 캐시할 수 없으므로, 새로고침 등의 브라우저의 기능을 이용할 때 데이터가 재전송될 가능성이 있습니다.

두 요청 방식은 각각 서로 다른 용도로 사용됩니다.
GET은 정보를 요청하고, POST는 데이터를 제출합니다.
이에 따라, 데이터의 보안성이나 전송 용량 등의 요소를 고려하여
적절한 요청 방식을 선택해야 합니다.

PUT, HEAD, DELETE는 HTTP 프로토콜에서 사용되는 요청 방식 중 일부입니다.

[PUT] PUT은 리소스를 업데이트하기 위해 사용됩니다.
- HTTP 본문(body)을 통해 데이터를 전달합니다.
- 요청한 리소스가 존재하면 업데이트하고, 존재하지 않으면 새로 생성합니다.
- PUT 요청은 idempotent(멱등)하다는 특징이 있습니다. 즉, 여러 번 요청을 해도 동일한 결과를 보장합니다.
- PUT 요청은 다른 클라이언트들에게 리소스가 업데이트되었음을 알리지 않습니다.

[HEAD] HEAD는 GET 요청과 유사하지만, 서버에서 응답으로 본문을 포함하지 않습니다.
- 서버에서 리소스의 메타데이터만을 가져올 수 있습니다.
- HEAD 요청은 리소스의 존재 여부, 변경 시간 등의 정보를 확인할 때 유용합니다.
- HEAD 요청은 GET 요청과 마찬가지로 캐시할 수 있습니다.

[DELETE] DELETE는 리소스를 삭제하기 위해 사용됩니다.
- 요청한 리소스가 존재하면 삭제하고, 존재하지 않으면 오류를 반환합니다.
- DELETE 요청은 idempotent하다는 특징이 있습니다. 즉, 여러 번 요청을 해도 동일한 결과를 보장합니다.
- DELETE 요청은 다른 클라이언트들에게 리소스가 삭제되었음을 알리지 않습니다.

이러한 요청 방식은 HTTP 프로토콜에서 리소스를
조작하는 다양한 방법을 제공합니다. 각 요청 방식은 자신의 특징과 용도가 있으며, 적절한 상황에서 적절한 요청 방식을 선택하여 사용해야 합니다.
*/