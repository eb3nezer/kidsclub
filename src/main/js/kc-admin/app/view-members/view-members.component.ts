import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { TeamService } from "../services/team.service";
import { Project } from "../model/project";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-view-members',
  templateUrl: './view-members.component.html',
  styleUrls: ['./view-members.component.css']
})
export class ViewMembersComponent implements OnInit {
    project: Project;
    displayedColumns = ['userName', 'userPhone', 'userEmail', 'userPhoto', 'userNotes'];

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private route: ActivatedRoute) {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`${project.name} Project Members`);
                this.appTitleService.setCurrentProject(project);
            });
        }
    }

  ngOnInit() {
  }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }

}
