package dev.tk2575;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
class ApplicationArgs {
    
    @Parameter(
            names = "--name",
            description = "The person receiving a hello",
            required = true
    )
    private String name;
}
