import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {AlbumService} from "../../shared/services/album.service";
import {Album} from "../../shared/model/album";

@Component({
  selector: 'view-albums',
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.css']
})
export class AlbumsComponent implements OnInit {
    project: Project;
    albums: Album[];
    displayedColumns = ['name', 'description', 'photoCount'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private albumService: AlbumService
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });

            this.albumService.getAllAlbumsForProject(projectId).subscribe(albums => this.albums = albums);
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Albums"));
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
