package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
//@Builder
public class ResponseRs<T> {
    private String error;
    private Long timestamp;
    private Integer offset;
    private Integer perPage;
    private T data;
    @JsonProperty("error_description")
    private String errorDescription;

    public ResponseRs(String error, int offset, int perPage, T data) {
        this.error = error;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseRs(String error, T data, String errorDescription){
        this.error = error;
        this.data = data;
        this.errorDescription = errorDescription;
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseRs(String error, String errorDescription){
        this.error = error;
        this.errorDescription = errorDescription;
    }
}
