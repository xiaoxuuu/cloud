package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.core.annotation.Wrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/csv")
public class DemoCsvController {

    @Wrap(disabled = true)
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadCsv() {
        List<Person> people = generatePeople(); // 假设有一个方法用于生成人员信息
        byte[] csvContent = generateCsvContent(people); // 生成CSV文件内容

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "relation.csv");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    private List<Person> generatePeople() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice", 30));
        people.add(new Person("Bob", 25));
        // 添加更多人员信息
        return people;
    }

    private byte[] generateCsvContent(List<Person> people) {

        return """
                startNodeCode,endNodeCode,type,name,code
                ORGANIZATION_210,PEOPLE_7, ORGANIZATION_CORE_MEMBER,核心人员,ORGANIZATION_210-PEOPLE_7
                """.getBytes();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Person {
        private String name;
        private Integer age;
    }
}