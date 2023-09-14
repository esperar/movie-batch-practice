package esperer.moviebatchstudy.batch.movie;

import esperer.moviebatchstudy.domain.MovieCmm;
import esperer.moviebatchstudy.domain.MovieCmmRepository;
import esperer.moviebatchstudy.domain.dto.KobisResponse;
import esperer.moviebatchstudy.domain.dto.MovieCompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class MovieConfiguration {

    private final MovieCmmRepository movieCmmRepository;
    private final WebClient webClient;
    private final EntityManagerFactory entityManagerFactory;
    private final JobBuilderFactory JobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job movieScrappingJob() {
        return JobBuilderFactory.get("movieScrappingJob")
                .preventRestart()
                .start(movieScrappingStep())
                .build();
    }

    @Bean
    public Step movieScrappingStep() {
        return stepBuilderFactory.get("movieScrappingStep").<MovieCompany, MovieCmm>chunk(30)
                .reader(movieScrapper())
                .processor(processor())
                .writer(insert())
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<MovieCompany> movieScrapper() {
        log.info("$$$$$$$$$ 배치 읽어오기 시작");
        int page = 1;
        List<MovieCompany> list = new ArrayList<>();
        for(;page < 4; page++) {
            ResponseEntity<KobisResponse> response = webClient.get().uri("/company/searchCompanyList.json?key=f5eef3421c602c6cb7ea224104795888&itemPerPage=10&curPage=\"+page")
                    .retrieve().toEntity(KobisResponse.class)
                    .block();

            assert response != null;
            list.addAll(response.getBody().companyListResult.getCompanyList());
        }
        return new ListItemReader<>(list);
    }

    public ItemProcessor<MovieCompany, MovieCmm> processor() {
        return item -> {
            log.info("~~~~~~~~~~~~~~~~~ 배치 프로세스 진행중");
            return item.toEntity();
        };
    }

    public JpaItemWriter<MovieCmm> insert() {
        JpaItemWriter<MovieCmm> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}
