import { Component } from '@angular/core';
import { AppTitleService } from "./services/app-title.service";
import {Project} from "./model/project";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  pageTitle: string = "";
  projectId: number;
  currentProject: Project;

  constructor(private apptitleService: AppTitleService) {
  }

  ngOnInit() {
    this.apptitleService.getTitleObserver().subscribe(title => {
        this.pageTitle = title;
        document.title = title;
      },
      e => console.log('on error %s', e),
      () => console.log('on complete'));

    this.apptitleService.getCurrentProjectObserver().subscribe(project => this.currentProject = project);
  }

  ngAfterViewInit() {
  }
}
