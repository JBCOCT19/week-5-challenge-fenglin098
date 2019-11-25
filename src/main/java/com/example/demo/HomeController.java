package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listJobs(Model model){
        model.addAttribute("jobs", jobRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String JobForm(Model model){
        model.addAttribute("job", new Job());
        return "jobform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Job job, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()){
            jobRepository.save(job);
            return "redirect:/";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            job.setPicture(uploadResult.get("url").toString());
            jobRepository.save(job);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showJob(@PathVariable("id") long id, Model model){
        model.addAttribute("job", jobRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateJob(@PathVariable("id") long id, Model model){
        model.addAttribute("job", jobRepository.findById(id).get());
        return "jobform";
    }

    @RequestMapping("delete/{id}")
    public String deleteJob(@PathVariable("id") long id, Model model){
        jobRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/result")
    public String search(Model model, @RequestParam("search") String search){
        model.addAttribute("jobs", jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search,search));
        return "result";
    }
}
