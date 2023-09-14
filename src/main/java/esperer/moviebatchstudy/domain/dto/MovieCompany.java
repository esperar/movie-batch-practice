package esperer.moviebatchstudy.domain.dto;

import esperer.moviebatchstudy.domain.MovieCmm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class MovieCompany {

    private long companyCd;
    private String companyNm;
    private String companyPartNames;

    public MovieCmm toEntity(){
        return MovieCmm.builder()
                .id(companyCd)
                .name(companyNm)
                .partName(companyPartNames)
                .build();
    }
}
