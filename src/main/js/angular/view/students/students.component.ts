import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Student} from "../../shared/model/student";
import {StudentService} from "../../shared/services/student.service";

@Component({
  selector: 'view-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit {
    project: Project;
    students: Student[];
    displayedColumns = ['name', 'warnings', 'team'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private studentService: StudentService
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });

            this.studentService.loadStudentsForProject(projectId).subscribe(students => this.students = students);
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Students"));
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
