import {AfterViewInit, Component, OnInit} from '@angular/core';
import { AppTitleService } from '../../shared/services/app-title.service';
import { ProjectService } from '../../shared/services/project.service';
import { Project } from '../../shared/model/project';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-edit-project',
  templateUrl: './edit-project.component.html',
  styleUrls: ['./edit-project.component.css']
})
export class EditProjectComponent implements OnInit, AfterViewInit {
    project: Project;
    mediaPermitted: boolean;
    sortByScore: boolean;
    wallboardColumns: number;
    disabled: boolean;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private location: Location,
        private snackBar: MatSnackBar) {
        this.project = new Project();
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.disabled = project.disabled === true;
                this.mediaPermitted = project.properties.studentMediaPermittedDefault === 'true';
                this.sortByScore = project.properties.sortTeamsByScore === 'true';
                if (project.properties.wallboardColumns) {
                    this.wallboardColumns = project.properties.wallboardColumns;
                }
                this.appTitleService.setTitle(`Edit details for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
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
            if (this.sortByScore) {
                this.project.properties.sortTeamsByScore = 'true';
            } else {
                this.project.properties.sortTeamsByScore = 'false';
            }
            if (this.wallboardColumns) {
                this.project.properties.wallboardColumns = this.wallboardColumns.toString();
            } else {
                this.project.properties.wallboardColumns = '0';
            }
            this.project.disabled = this.disabled;

            this.projectService.updateProject(this.project.id, this.project).subscribe(project => {
                this.project = project;
                this.mediaPermitted = project.properties.studentMediaPermittedDefault === 'true';
                this.appTitleService.setTitle(`Edit details for ${project.name}`);
                this.snackBar.open('Project updated', 'Dismiss', {
                    duration: 10000,
                });
            });
        }
    }

    onCancel() {
        this.location.back();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }
}
