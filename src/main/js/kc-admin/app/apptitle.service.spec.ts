import { TestBed, inject } from '@angular/core/testing';

import { ApptitleService } from './apptitle.service';

describe('ApptitleService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApptitleService]
    });
  });

  it('should be created', inject([ApptitleService], (service: ApptitleService) => {
    expect(service).toBeTruthy();
  }));
});
