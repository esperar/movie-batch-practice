package esperer.moviebatchstudy.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class KobisResponse {

    private BoxOfficeResult boxOfficeResult;
    public CompanyResult companyListResult;

    @Getter
    @ToString
    private static class BoxOfficeResult {
        private String boxOfficeType;
        private String showRange;
        private List<BoxOfficeDto> dailyBoxOfficeList;
    }

    @Getter
    @ToString
    public static class CompanyResult {
        private int totCnt;
        private List<MovieCompany> companyList;
    }

}