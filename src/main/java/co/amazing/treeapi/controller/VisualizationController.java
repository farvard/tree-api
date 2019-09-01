package co.amazing.treeapi.controller;


import co.amazing.treeapi.service.VisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("visualization")
public class VisualizationController {

    private final VisualizationService service;

    @Autowired
    public VisualizationController(VisualizationService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[] csv() {
        return service.csv().getBytes();
    }

}

