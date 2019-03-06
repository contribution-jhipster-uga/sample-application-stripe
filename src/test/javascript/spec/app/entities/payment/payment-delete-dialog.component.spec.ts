/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { StripeTestModule } from '../../../test.module';
import { PaymentDeleteDialogComponent } from 'app/entities/payment/payment-delete-dialog.component';
import { PaymentService } from 'app/entities/payment/payment.service';

describe('Component Tests', () => {
    describe('Payment Management Delete Component', () => {
        let comp: PaymentDeleteDialogComponent;
        let fixture: ComponentFixture<PaymentDeleteDialogComponent>;
        let service: PaymentService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [StripeTestModule],
                declarations: [PaymentDeleteDialogComponent]
            })
                .overrideTemplate(PaymentDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(PaymentDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PaymentService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
