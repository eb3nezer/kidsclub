import { Component, OnInit } from '@angular/core';
import {ProjectService} from "../../shared/services/project.service";
import {ActivatedRoute} from "@angular/router";
import {AppTitleService} from "../../shared/services/app-title.service";
import {Location} from "@angular/common";
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent implements OnInit {
    projectName: string;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private location: Location,
        private snackBar: MatSnackBar,
        private router: Router) {
    }

    ngOnInit() {
    }

    onSubmit() {
        this.projectService.createProject(this.projectName).subscribe(project => {
            if (project) {
                this.snackBar.open(`Project created`, 'Dismiss', {
                    duration: 10000,
                });
                this.router.navigate(["viewproject", project.id]);
            }
        })
    }

    onCancel() {
        this.location.back();
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Create Project"));
    }
}
