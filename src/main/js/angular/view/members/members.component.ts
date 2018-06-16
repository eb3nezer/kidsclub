import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../shared/model/user";

@Component({
  selector: 'view-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.css']
})
export class MembersComponent implements OnInit {
    project: Project;
    projectUsers: User[];
    displayedColumns = ['userName', 'userPhone', 'userEmail', 'userPhoto'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService
    ) {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.projectUsers = project.users;
                this.appTitleService.setCurrentProject(project);
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Members"));
        Promise.resolve(null).then(() => this.loadProject());
    }
}
