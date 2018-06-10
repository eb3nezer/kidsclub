import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { Project } from "../model/project";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-edit-project',
  templateUrl: './edit-project.component.html',
  styleUrls: ['./edit-project.component.css']
})
export class EditProjectComponent implements OnInit {
    project: Project;
    mediaPermitted: boolean;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private route: ActivatedRoute) {
        this.project = new Project();
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.mediaPermitted = project.properties.studentMediaPermittedDefault === 'true';
                this.appTitleService.setTitle(`Edit details for ${project.name}`);
            });
        }
    }

    onSubmit() {
        if (this.project) {
            if (this.mediaPermitted) {
                this.project.properties.studentMediaPermittedDefault = 'true';
            } else {
                this.project.properties.studentMediaPermittedDefault = 'false';
            }

            this.projectService.updateProject(this.project.id, this.project).subscribe(project => {
                this.project = project;
                this.mediaPermitted = project.properties.studentMediaPermittedDefault === 'true';
                this.appTitleService.setTitle(`Edit details for ${project.name}`);
                this.appTitleService.setMessages("Project updated");
            })
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }

}
