package cat.community.NyangMunity.meme.tasklet;

import cat.community.NyangMunity.meme.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class getCatMemeTasklet implements Tasklet {

    private final MemeService memeService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        return RepeatStatus.FINISHED;
    }
}

