import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import { Location } from '@angular/common';
import {MatSnackBar} from "@angular/material";

import { AppTitleService } from "../../shared/services/app-title.service";
import {ProjectService} from "../../shared/services/project.service";
import {Project} from "../../shared/model/project";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'app-create-team',
  templateUrl: './create-team.component.html',
  styleUrls: ['./create-team.component.css']
})
export class CreateTeamComponent implements OnInit {
    project: Project;
    team: StudentTeam;
    fileToUpload: File;
    newFilename: string;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private teamService: TeamService,
        private snackBar: MatSnackBar) {
        this.team = new StudentTeam();
    }

    ngOnInit() {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Create a team for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
            });
        }
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    onSubmit() {
        this.teamService.createTeam(this.team, this.project.id).subscribe(team => {
            if (team.id) {
                this.snackBar.open(`New team created`, 'Dismiss', {
                    duration: 10000,
                });
                this.router.navigate(["viewteam", this.project.id, "team", team.id]);
                this.newFilename = undefined;
            }
        });
    }

    onCancel() {
        this.location.back();
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }
}
