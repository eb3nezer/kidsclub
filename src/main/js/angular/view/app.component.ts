import { Component } from '@angular/core';
import {AppTitleService} from "../shared/services/app-title.service";
import {Project} from "../shared/model/project";

@Component({
  selector: 'view-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
    pageTitle = "Hello";
    currentProject: Project;

    constructor(private appTitleService: AppTitleService) {

    }
    ngOnInit() {
        this.appTitleService.getTitleObserver().subscribe(title => {
                this.pageTitle = title;
                document.title = title;
            },
            e => console.log('on error %s', e),
            () => console.log('on complete'));

        this.appTitleService.getCurrentProjectObserver().subscribe(project => this.currentProject = project);
    }

}
